package com.mits.subscription.data.db.migration

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

/**
 * Adds sync bookkeeping columns to every table: a stable client-generated [remoteId] (the identity
 * used once a backend exists to sync across devices — the existing Long primary keys stay purely
 * local and keep working for navigation/FKs unchanged), [updatedAt] for delta sync, and [deletedAt]
 * reserved for a future soft-delete (not wired into any query yet, deliberately — see PR description).
 *
 * remoteId can't use a SQL column DEFAULT (each existing row needs its own random UUID), so existing
 * rows are backfilled one by one after the column is added.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        addSyncColumns(db, "workshop")
        addSyncColumns(db, "subscription")
        addSyncColumns(db, "lesson")

        backfillRemoteIds(db, table = "workshop", idColumn = "workshop_id")
        backfillRemoteIds(db, table = "subscription", idColumn = "sub_id")
        backfillRemoteIds(db, table = "lesson", idColumn = "lId")

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_workshop_remoteId ON workshop(remoteId)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_subscription_remoteId ON subscription(remoteId)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_lesson_remoteId ON lesson(remoteId)")
    }

    private fun addSyncColumns(db: SupportSQLiteDatabase, table: String) {
        db.execSQL("ALTER TABLE $table ADD COLUMN remoteId TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE $table ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE $table ADD COLUMN deletedAt INTEGER")
    }

    private fun backfillRemoteIds(db: SupportSQLiteDatabase, table: String, idColumn: String) {
        val now = System.currentTimeMillis()
        val ids = mutableListOf<Long>()
        db.query("SELECT $idColumn FROM $table").use { cursor ->
            while (cursor.moveToNext()) {
                ids += cursor.getLong(0)
            }
        }
        for (id in ids) {
            val values = ContentValues().apply {
                put("remoteId", UUID.randomUUID().toString())
                put("updatedAt", now)
            }
            db.update(table, SQLiteDatabase.CONFLICT_ABORT, values, "$idColumn = ?", arrayOf<Any?>(id))
        }
    }
}

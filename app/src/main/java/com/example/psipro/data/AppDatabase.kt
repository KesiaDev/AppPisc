package com.example.psipro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.psipro.data.converters.DateConverter
import com.example.psipro.data.converters.LocalDateTimeConverter
import com.example.psipro.data.converters.StringListConverter
import com.example.psipro.data.converters.StringMapConverter
import com.example.psipro.data.dao.*
import com.example.psipro.data.entities.*
import com.example.psipro.data.entities.WhatsAppConversation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Database(
    entities = [
        Patient::class,
        PatientNote::class,
        PatientMessage::class,
        PatientReport::class,
        Appointment::class,
        User::class,
        AuditLog::class,
        FinancialRecord::class,
        WhatsAppConversation::class,
        Prontuario::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(DateConverter::class, LocalDateTimeConverter::class, StringListConverter::class, StringMapConverter::class)
@Singleton
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun patientNoteDao(): PatientNoteDao
    abstract fun patientMessageDao(): PatientMessageDao
    abstract fun patientReportDao(): PatientReportDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun userDao(): UserDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun financialRecordDao(): FinancialRecordDao
    abstract fun whatsappConversationDao(): WhatsAppConversationDao
    abstract fun prontuarioDao(): ProntuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE patients ADD COLUMN sessionValue REAL NOT NULL DEFAULT 0.0")
            }
        }

        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE appointments ADD COLUMN sessionValue REAL NOT NULL DEFAULT 0.0")
            }
        }

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "psipro_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
} 
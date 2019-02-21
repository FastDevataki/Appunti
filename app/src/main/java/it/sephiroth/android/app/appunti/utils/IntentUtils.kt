package it.sephiroth.android.app.appunti.utils

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import it.sephiroth.android.app.appunti.CategoriesEditActivity
import it.sephiroth.android.app.appunti.DetailActivity
import it.sephiroth.android.app.appunti.PreferencesActivity
import it.sephiroth.android.app.appunti.SearchableActivity
import it.sephiroth.android.app.appunti.db.tables.Attachment
import it.sephiroth.android.app.appunti.db.tables.Entry
import it.sephiroth.android.app.appunti.ext.getFileUri
import timber.log.Timber

object IntentUtils {

    const val KEY_ENTRY_ID = "entryID"
    const val KEY_REMOVE_ALARM = "removeAlarm"
    const val KEY_QUERY = SearchManager.QUERY
    const val KEY_CATEGORY_ID = "categoryID"

    const val ACTION_ASK_NEW_CATEGORY_STARTUP = "ask_for_new_category_startup"

    fun createNewEntryIntent(context: Context): Intent {
        return Intent(context, DetailActivity::class.java).apply {
            action = Intent.ACTION_CREATE_DOCUMENT
        }
    }

    fun createViewEntryIntent(context: Context, entryID: Long, removeAlarm: Boolean? = false): Intent {
        return Intent(context, DetailActivity::class.java).apply {
            action = Intent.ACTION_EDIT
            putExtra(KEY_ENTRY_ID, entryID)
            removeAlarm?.let {
                putExtra(KEY_REMOVE_ALARM, it)
            }
        }
    }

    fun createShareEntryIntent(context: Context, entry: Entry): Intent {
        return Intent(android.content.Intent.ACTION_SEND).apply {
            type = FileSystemUtils.TEXT_MIME_TYPE
            putExtra(android.content.Intent.EXTRA_SUBJECT, entry.entryTitle)
            putExtra(android.content.Intent.EXTRA_TEXT, entry.entryText)
        }
    }

    // Categories Intent Builder

    class Categories {

        class Builder(context: Context) {
            val intent: Intent = Intent(context, CategoriesEditActivity::class.java)

            fun createNewCategory(): Builder {
                intent.removeExtra(KEY_CATEGORY_ID)
                intent.action = ACTION_ASK_NEW_CATEGORY_STARTUP
                return this
            }

            fun pickCategory(): Builder {
                intent.action = Intent.ACTION_PICK
                return this
            }

            fun selectedCategory(categoryID: Long): Builder {
                intent.putExtra(KEY_CATEGORY_ID, categoryID)
                return this
            }

            fun build() = intent
        }
    }


    fun createPerferencesIntent(context: Context): Intent {
        return Intent(context, PreferencesActivity::class.java)
    }

    fun createSearchableIntent(context: Context, query: String? = null): Intent {
        return Intent(context, SearchableActivity::class.java).apply {
            query?.let { query ->
                action = Intent.ACTION_SEARCH
                putExtra(KEY_QUERY, query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    fun createPickDocumentIntent(context: Context): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
    }

    fun createPickImageIntent(context: Context): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
    }

    fun createAttachmentShareIntent(context: Context, attachment: Attachment): Intent {
        val finalUri = attachment.getFileUri(context)
        Timber.i("finalUri: ${finalUri.toString()}")
        return Intent(Intent.ACTION_SEND).apply {
            putExtra(android.content.Intent.EXTRA_SUBJECT, attachment.attachmentTitle)
            putExtra(android.content.Intent.EXTRA_STREAM, finalUri)
            setDataAndType(finalUri, attachment.attachmentMime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun createAttachmentViewIntent(context: Context, attachment: Attachment): Intent {
        val finalUri = attachment.getFileUri(context)
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(finalUri, attachment.attachmentMime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

}
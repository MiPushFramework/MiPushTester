package moe.yuuta.mipushtester.push.internal

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class CoreProvider : ContentProvider() {
    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun query(p0: Uri, p1: Array<String>?, p2: String?, p3: Array<String>?, p4: String?): Cursor? {
        throw UnsupportedOperationException()
    }

    override fun onCreate(): Boolean {
        PushSdkWrapper.setup(context!!)
        return true
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun getType(p0: Uri): String? {
        throw UnsupportedOperationException()
    }

}
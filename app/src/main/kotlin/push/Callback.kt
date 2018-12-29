package moe.yuuta.mipushtester.push

interface Callback {
    fun onPreExecute()
    fun onPostExecute(result: Exception?)
}

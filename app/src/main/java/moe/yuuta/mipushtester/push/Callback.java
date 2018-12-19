package moe.yuuta.mipushtester.push;

interface Callback {
    void onPreExecute ();
    void onPostExecute (Exception result);
}

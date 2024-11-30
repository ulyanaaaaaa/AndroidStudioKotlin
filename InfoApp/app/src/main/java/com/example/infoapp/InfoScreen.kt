package com.example.infoapp

import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.infoapp.utils.ListItem

@Composable
fun InfoScreen(item: ListItem?) {
    val configuration = LocalConfiguration.current

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (item != null) {
                val imageHeight = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
                    150.dp
                else
                    300.dp

                AssetImage(
                    imageName = item.imageName,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                )
            }
            if (item != null) {
                HtmlLoader(htmlName = item.htmlName)
            }
        }
    }
}

@Composable
fun HtmlLoader(htmlName: String) {
    var backEnabled by remember { mutableStateOf(false) }
    var webView: WebView? = null
    val context = LocalContext.current
    val assetManager = context.assets
    val inputStream = assetManager.open("html/$htmlName")
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val htmlString = String(buffer)

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        factory = { WebView(it).apply {
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()
                    if (URLUtil.isNetworkUrl(url)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                        return true
                    }
                    return false
                }
            }
        } },
        update = { webView ->
            webView.loadData(htmlString, "text/html", "utf-8")
        }
    )
}


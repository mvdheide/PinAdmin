package eu.centric.pinadmin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
/**
 * HelpActivity contains only a webview element and it shows help.html from assets.
 *
 * @author MHeide
 * @since 10-12-‎2017
 */
public class HelpActivity extends AppCompatActivity {

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Perform initialization of all fragments and loaders. Init the webview with the file help.html
     *
     * @param savedInstanceState the Activity previous frozen state, if there was one.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView mWebView = (WebView) findViewById(R.id.helpWebView);
//        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/help.html");
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}

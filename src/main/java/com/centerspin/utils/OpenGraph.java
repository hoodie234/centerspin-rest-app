package com.centerspin.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class OpenGraph {

    private final String url;
    private final int timeoutMillis = 5000;

    private OpenGraph(String url) {
        this.url = url;
    }

    public static JSONObject getArticleData(String url) throws IOException {
        OpenGraph graph = new OpenGraph(url);
        return graph.getArticleData();
    }
    

    private JSONObject getArticleData() throws IOException {
        
        String html = readHtmlSource(url);

        Document document = Jsoup.parse(html);
        document.setBaseUri(url);

        JSONObject jo = new JSONObject();
        
        jo.put(Constants.url, url);
        jo.put(Constants.title, getTitle(document));
        jo.put(Constants.description, getDescription(document));
        jo.put(Constants.source, getDomain(url));
        jo.put(Constants.image, getImage(document));
        
        return jo;
    }

    private String getImage(Document doc) {
        // <meta property="og:image" content="*" />
        Elements elements = doc.getElementsByAttributeValue("property", "og:image");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the image : <meta property=\"og:image\" content=\"*\" />");
                return getValidPath(text);
            }
        }

        // 2nd -> img in div
        for (Element e1 : doc.getElementsByTag("div")) {
            if (e1.children().size() > 0) {
                e1 = e1.child(0);
                if (e1.tagName().equals("img")) {
                    if (e1.hasAttr("width")) {
                        final String text = getValidPath(e1.attr("src"));
                        if (isNotEmpty(text)) return getValidPath(text);
                    }
                }
            }
        }

        // 2nd -> img in p
        for (Element e1 : doc.getElementsByTag("p")) {
            for (Element e2 : e1.getElementsByTag("img")) {
                if (e2.hasAttr("src")) {
                    final String text = getValidPath(e2.attr("src"));
                    if (isNotEmpty(text)) return getValidPath(text);
                }
            }
        }


        // 2nd -> img in dd
        for (Element e1 : doc.getElementsByTag("dd")) {
            for (Element e2 : e1.getElementsByTag("img")) {
                if (e2.hasAttr("src")) {
                    final String text = getValidPath(e2.attr("src"));
                    if (isNotEmpty(text)) return getValidPath(text);
                }
            }
        }

        // 3rd -> img in html
        for (Element e : doc.getElementsByTag("img")) {
            if (e.hasAttr("src")) {
                final String text = getValidPath(e.attr("src"));
                if (isNotEmpty(text)) return getValidPath(text);
            }
        }

        // etc empty
        return "";
    }

    public String getDomain(String url) {
        try {
            // 1st uri parse
            return new URI(url).getHost();
        } catch (Exception e) {
            // etc empty
            e.printStackTrace();
            return "";
        }
    }

    private String getValidPath(String url) {
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return url;
            }

            final URI ogpUri = new URI(this.url);
            final URI imgUri = ogpUri.resolve(url);
            return imgUri.toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }



    /*
     */


    private String getTitle(Document doc) {
        // <meta property="og:title" content="*" />
        Elements elements = doc.getElementsByAttributeValue("property", "og:title");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the title : <meta property=\"og:title\" content=\"*\" />");
                return text;
            }
        }

        // <meta name="title" content="*">
        elements = doc.getElementsByAttributeValue("name", "title");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the title : <meta name=\"title\" content=\"*\">");
                return text;
            }
        }

        // <title>*</title>
        String title = doc.title();
        if (isNotEmpty(title)) {
            printLog("parse the title : <title>*</title>");
            return title;
        }

        // return empty
        printLog("parse the title fail");
        return "";
    }

    private String getDescription(Document doc) {
        // <meta property="og:description" content="*" />
        Elements elements = doc.getElementsByAttributeValue("property", "og:description");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the description : <meta property=\"og:description\" content=\"*\" />");
                return text;
            }
        }

        // <meta name="description" content="*">
        elements = doc.getElementsByAttributeValue("name", "description");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the description : <meta name=\"description\" content=\"*\">");
                return text;
            }
        }

        // <p>*</p>
        for (Element e : doc.getElementsByTag("p")) {
            if (e.hasText() && isNotEmpty(e.text())) {
                printLog("parse the description : <p>*</p>");
                return e.text();
            }
        }

        // <div>*</div>
        for (Element e : doc.getElementsByTag("div")) {
            if (e.hasText() && isNotEmpty(e.text())) {
                printLog("parse the description : <div>*</div>");
                return e.text();
            }
        }

        // return empty
        printLog("parse the description fail");
        return "";
    }


    /*
     */

    private String readHtmlSource(String url) throws IOException {
        // read byte source
        ByteArrayOutputStream source = _readsByteSource(url, true);
        printLog("html reading is successful");

        // parse charset
        String charset = _parseCharset(source);
        printLog("charset parsing is successful : " + charset);

        // encoding and return
        return source.toString(charset);
    }


    private ByteArrayOutputStream _readsByteSource(String url, boolean isRecursive) throws IOException {
        // print the log
        printLog("html reads the source : " + url);

        // url connection settings
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(timeoutMillis);
        connection.setRequestProperty("User-agent", "");
        connection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());

        // reads the byte source
        int b;
        InputStream is = connection.getInputStream();
        ByteArrayOutputStream source = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
            source.write(b);
        }
        is.close();

        // not called recursive function
        if (!isRecursive) {
            return source;
        }

        // html parse
        Document document = Jsoup.parse(source.toString());

        // if have <meta property="og:url" content="*" />, it will call recursive
        for (Element e : document.getElementsByAttributeValue("property", "og:url")) {
            if (e.hasAttr("content")) {
                final String text = e.attr("content").trim();
                if (isNotEmpty(text) && !url.equals(text)) {
                    printLog("html reads again the source : <meta property=\"og:url\" content=\"*\" />");
                    return _readsByteSource(getValidPath(text), true);
                }
            }
        }

        // if have <frame src="*"></frame>, it will call recursive
        for (Element e : document.getElementsByTag("frame")) {
            if (e.hasAttr("src")) {
                final String text = e.attr("src");
                if (isNotEmpty(text)) {
                    printLog("html reads again the source : <frame src=\"*\"></frame>");
                    return _readsByteSource(getValidPath(text), false);
                }
            }
        }

        // else, return byte source
        return source;
    }


    private String _parseCharset(ByteArrayOutputStream source) {
        // html parse
        Document document = Jsoup.parse(source.toString());

        // <meta http-equiv="content-type" content="charset=*" />
        for (Element e : document.getElementsByAttributeValue("http-equiv", "content-type")) {
            if (e.hasAttr("content")) {
                final String text = e.attr("content");
                if (isNotEmpty(text)) {
                    for (String str : text.split(Pattern.quote(";"))) {
                        if (str.contains("charset=")) {
                            final String charset = str.trim().replaceAll(Pattern.quote("charset="), "");
                            if (isNotEmpty(charset)) {
                                printLog("charset parse : <meta http-equiv=\"content-type\" content=\"charset=*\" />");
                                return charset;
                            }
                        }
                    }
                }
            }
        }

        // <meta charset="*" />
        for (Element e : document.getElementsByAttribute("charset")) {
            final String charset = e.attr("charset");
            if (isNotEmpty(charset)) {
                printLog("charset parse : '<meta charset=\"*\" />'");
                return charset;
            }
        }

        // not found
        printLog("charset parse : not found.");
        return "utf-8";
    }


    private boolean isNotEmpty(String str) {
        // if trim string is not empty, return true
        return str != null && str.trim().length() != 0;
    }


    private void printLog(String msg) {
        // if have logger, print log
//        if (logger != null) logger.log(tag, msg);
    }
}
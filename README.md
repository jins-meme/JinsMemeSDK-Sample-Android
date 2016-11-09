# Android向け JINS MEME SDK サンプルアプリ

まばたきと6軸（加速度およびジャイロセンサー）の値のうちrollを可視化するAndroid向けサンプルアプリです。

## 対象環境

[Android Studio](https://developer.android.com/studio/)を使用されていることを想定しています。 このレポジトリをクローンするかZIP形式でダウンロードしてください。

## セットアップ

### JINSアカウントを作成
まだお持ちでない方は、[JINS MEMEのデベロッパー向けダッシュボード](https://developers.jins.com/)で[JINSアカウントを作成](https://developers.jins.com/preregistration/)いただく必要があります。

### SDKのダウンロード
[JINS MEMEのデベロッパー向けダッシュボード](https://developers.jins.com/)においてJINSアカウントでログイン後、[SDKをダウンロード](https://developers.jins.com/sdks/android/)して、プロジェクトの `app/libs` ディレクトリに `app/libs/MemeLib.jar` となるようにコピーしてください。

### アプリを作成して、アプリIDとアプリSecretを取得
[JINS MEMEのデベロッパー向けダッシュボード](https://developers.jins.com/)で[アプリを作成](https://developers.jins.com/ja/apps/)して、`app/src` ディレクトリ以下の *"com.jins_meme.visualizing_blinks_and_6axis.LiveViewActivity.java"* の次の部分をアプリIDとアプリSecretで置き換えてください

```java
// TODO : Replace APP_ID and APP_SECRET
private static final String APP_ID = "App ID";
private static final String APP_SECRET = "App Secret";
```


## License
MIT

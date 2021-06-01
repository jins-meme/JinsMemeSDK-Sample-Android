# Android向け JINS MEME SDK サンプルアプリ

## 【重要】サポート終了のお知らせ

現行版 JINS MEME は2021年3月末をもちましてサポートを終了しています。SDKにつきましても2021年9月末をもって動作しなくなりますのでご注意ください。

https://jins-meme.com/ja/support/user-support/jins-meme%E3%81%AE%E3%82%B5%E3%83%9D%E3%83%BC%E3%83%88%E7%B5%82%E4%BA%86%E3%81%AB%E3%81%A4%E3%81%84%E3%81%A6/

## 概要

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

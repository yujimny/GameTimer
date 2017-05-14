# GameTimer
* 説明
  * GameTimerはかの有名な某名人のお言葉、「ゲームは1日1時間」を忠実に守るために作られたタイマーアプリです。
  * Pochiruというデバイスが別途必要です。
  * Pochiruのボタンを一度押すとタイマー開始、長押しで終了です。設定した時間になると音声とLED点灯で通知されます。終了しないと数分おきに催促され続けます。
  * 初期設定は30分ですがスマホUIから変更可能です。
  * 利用するためにはLinkingアプリを別途ダウンロード、設定する必要があります。

* コンセプト
  * 基本的にスマホに触ることなく、Pochiruというデバイスとスマホから出る音声のみでUIを実現しています。
  * スマホはロックしたままで構いません。子供でも簡単に操作できることを目指しています。
  * PochiruとProject Linkingを使って何かできないかと試作したサンプルです。

* リンク
  * [LinkingアプリとPochiruの設定方法](http://frontier.fxy-co.com/LinkingIFDocument/android_setting.html)
  * [Project Linkng開発者サイト](https://linkingiot.com/developer/index.html)
  * [Pochiru](http://www.products.braveridge.com/pochiru/)

* 必要なもの
  * Pochiru等のLinking対応端末(ボタンとLED付きのもの)
  * Linkingアプリ
  * (必要に応じて)日本語音声合成エンジン(Android 6.0あたりから日本語にも標準で対応している気がします、無い場合は N2 TTS がおすすめです。)
  
* 今後(アイデア)
  * iRemocon等と連携して強制TV電源断

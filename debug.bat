@echo off

set userpath=%~dp0\conf

@echo 開発環境デバッグ設定でサーバを起動します

cmd.exe /k activator -jvm-debug 9999 run

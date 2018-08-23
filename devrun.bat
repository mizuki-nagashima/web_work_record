@echo off

set userpath=%~dp0\conf

@echo 開発環境設定でサーバを起動します

cmd.exe /k activator run

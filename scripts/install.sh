#!/bin/sh

BIN_DIR=${XDG_CONFIG_DATA:-$HOME/.local}/bin
chmod 755 bin/crypto-egg
cp bin/crypto-egg $BIN_DIR

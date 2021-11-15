(ns cli.core
  (:require
    [cljs.core :as cljs]
    ["yargs" :as yargs]
    [cli.web3 :as web3]))


(defn handle-token-agg [token]
  (->
    (web3/create)
    (web3/get-token-usd token)
    (.then print)
    (.then web3/close)
    (.then js/process.exit)))

(defn handle-gas-price []
  (->
    (web3/create)
    (web3/get-gas)
    (.then print)
    (.then web3/close)
    (.then js/process.exit)))

(defn get-args
  ([] (get-args nil))
  ([ex]
   (->
     yargs
     (.scriptName "crypto-egg")
     (.command
       (cljs/clj->js ["$0 [token]" "agg [token]"])
       "get current aggregate price of a token in terms of USD"
       (fn [^js/yargs yargs]
         (->
           yargs
           (.positional
             "token"
             (cljs/clj->js
               {:default "eth"
                :defaultDescription "Look up the current aggregate price of Ethereum to fiat USD"
                :description "token to look up"
                :type "string"})))))
     (.command
       "gas"
       "get the current Ethereum fast-gas price in GWei"
       (cljs/clj->js {}))
     (.parse (or ex js/undefined))
     (cljs/js->clj :keywordize-keys true))))

(comment
  (get-args "eth")
  (get-args "btc")
  (get-args "agg")
  (get-args "gas"))

(defn ^:export main []
  (let [argv (get-args)
        token (:token argv)]
    (if
      token (handle-token-agg token)
      (handle-gas-price))))

(comment
  (main))

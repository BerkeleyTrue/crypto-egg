(ns cli.web3
  (:require
    ["web3" :as web3js]
    ["eth-provider" :as eth-provider]))

(defn create
  "Create a web3 instance"
  []
  (new web3js (eth-provider)))

(defn get-gas [web3]
  (->
    web3
    ^js/web3js.eth (.-eth)
    (.getGasPrice)
    (.then #(/ %1 (js/Math.pow 10 9)))))

(comment
  (->
    (create)
    (get-gas)
    (.then print)))

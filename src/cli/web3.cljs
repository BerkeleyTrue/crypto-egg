(ns cli.web3
  (:require
    [cljs.core :as cljs]
    [cljs.core.async :refer [go]]
    [cljs.core.async.interop :refer-macros [<p!]]
    ["web3" :as web3js]
    ["eth-provider" :as eth-provider]))

(def aggregatorV3InterfaceABI
  (cljs/clj->js
    [{"inputs" []
      "name" "decimals"
      "outputs" [{ "internalType" "uint8" "name" "" "type" "uint8"}]
      "stateMutability" "view"
      "type" "function"}
     {"inputs" []
      "name" "description"
      "outputs" [{ "internalType" "string" "name" "" "type" "string"}]
      "stateMutability" "view"
      "type" "function"}
     {"inputs" [{"internalType" "uint80" "name" "_roundId" "type" "uint80"}]
      "name" "getRoundData"
      "outputs" [{"internalType" "uint80" "name" "roundId" "type" "uint80"}
                 { "internalType" "int256" "name" "answer" "type" "int256"}
                 { "internalType" "uint256" "name" "startedAt" "type" "uint256"}
                 { "internalType" "uint256" "name" "updatedAt" "type" "uint256"}
                 { "internalType" "uint80" "name" "answeredInRound" "type" "uint80"}]
      "stateMutability" "view"
      "type" "function"}
     {"inputs" []
      "name" "latestRoundData"
      "outputs" [{"internalType" "uint80"
                  "name" "roundId"
                  "type" "uint80"}
                 {"internalType" "int256"
                  "name" "answer"
                  "type" "int256"}
                 {"internalType" "uint256"
                  "name" "startedAt"
                  "type" "uint256"}
                 {"internalType" "uint256"
                  "name" "updatedAt"
                  "type" "uint256"}
                 {"internalType" "uint80"
                  "name" "answeredInRound"
                  "type" "uint80"}]
      "stateMutability" "view"
      "type" "function"}
     {"inputs" []
      "name" "version"
      "outputs" [{ "internalType" "uint256" "name" "" "type" "uint256"}]
      "stateMutability" "view"
      "type" "function"}]))

(defn create
  "Create a web3 instance"
  []
  (new web3js (eth-provider)))

(defn get-gas [^js/web3js web3]
  (->
    web3
    (.-eth)
    (.getGasPrice)
    (.then #(/ %1 (js/Math.pow 10 9)))))

(comment
  (->
    (create)
    (get-gas)
    (.then print)))

(defn get-token-usd-addr [^js/web3js web3 token]
  (let [token-pair (str token "-usd.data.eth")]
    (->
      web3
      (.-eth)
      (.-ens)
      (.getAddress token-pair))))

(comment
  (->
    (create)
    (get-token-usd-addr "eth")
    (.then print)))

(defn create-agg-contract [^js/web3js web3 addr]
  (->
    web3
    (.-eth)
    (.-Contract)
    (#(new %1 aggregatorV3InterfaceABI addr))))

(comment
  (let [web3 (create)]
    (->
      web3
      (get-token-usd-addr "eth")
      (.then #(create-agg-contract web3 %1))
      (.then print))))

(defn get-decimals [^js/web3js.eth.contract contract]
  (->
    contract
    (.-methods)
    (.decimals)
    (.call)))

(comment
  (let [web3 (create)]
    (->
      web3
      (get-token-usd-addr "eth")
      (.then #(create-agg-contract web3 %1))
      (.then get-decimals)
      (.then print))))

(defn get-lastest-round [^js/web3js.eth.contract contract]
  (->
    contract
    (.-methods)
    (.latestRoundData)
    (.call)))

(comment
  (let [web3 (create)]
    (->
      web3
      (get-token-usd-addr "eth")
      (.then #(create-agg-contract web3 %1))
      (.then get-lastest-round)
      (.then js/console.log))))

(defn get-token-usd [^js/web3js web3 token]
  (->
    web3
    (get-token-usd-addr token)
    (.then #(create-agg-contract web3 %1))
    (.then
      (fn [^js/web3js.eth.Contract contract]
        (->
          #js [(get-lastest-round contract) (get-decimals contract)]
          (js/Promise.all)
          (.then
            (fn [[latest-round decimals-string]]
              (let [decimals (js/parseInt decimals-string)
                    answer (js/parseInt (.-answer latest-round))]
                (/ answer (js/Math.pow 10 decimals))))))))))
(comment
  (let [web3 (create)]
    (->
      web3
      (get-token-usd "eth")
      (.then print))))

(ns cli.web3
  (:require
    ["web3" :as web3js]
    ["eth-provider" :as eth-provider]
    [cli.utils :refer [display-price]]
    [cli.chain-link :refer [aggregatorV3InterfaceABI]]))

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
            (fn [[^js/Object latest-round decimals-string]]
              (let [decimals (js/parseInt decimals-string)
                    answer (js/parseInt (.-answer latest-round))]
                (->
                  answer
                  (/ (js/Math.pow 10 decimals))
                  (display-price))))))))))

(comment
  (let [web3 (create)]
    (->
      web3
      (get-token-usd "ada")
      (.then print))))

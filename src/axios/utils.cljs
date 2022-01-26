(ns axios.utils
  (:require
    [utils]))

(defn config->js [conf]
  (let [base-url (:base-url conf)
        ^js/Object js-map (utils/cljkk->js conf)]
    (when base-url
      (set! (. js-map -baseURL) base-url))
    js-map))

(defn is-client? [client?]
  (fn? (.-request client?)))

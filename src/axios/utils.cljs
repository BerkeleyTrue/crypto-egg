(ns axios.utils
  (:require
    [utils]))

(defn config->js [conf]
  (let [base-url (:base-url conf)
        headers (clj->js (:headers conf))
        conf (dissoc conf :base-url :headers)
        ^js/Object js-map (utils/cljkk->js conf)]

    (when base-url
      (set! (. js-map -baseURL) base-url))
    (when headers
      (set! (. js-map -headers) headers))

    js-map))

(defn is-client? [client?]
  (fn? (.-request client?)))

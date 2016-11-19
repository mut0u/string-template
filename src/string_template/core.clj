(ns string-template.core
  (:require [clojure.walk :refer [prewalk]]))


(def match-regx #"(\#\{(.*?)\})|(\#\[(.*?)\])")


(defn array-mark [v]
  (str "(" (clojure.string/join "," (repeat (count v) "?")) ")"))


(defn build-sql-construct [sql-str]
  (let [m (re-matcher match-regx sql-str)]
    (loop [index 0
           find? (.find m)
           result [[] []]]
      (if find?
        (let [substr (.substring sql-str index (.start m))
              [_ _ k _ v] (re-groups m)]
          (if k
            (let [end-index (.end m)
                  start-index (.start m)]
              (recur end-index
                     (.find m)
                     [(conj (first result) (str substr "?"))
                      (into (second result) `[~(symbol k)])]))
            (let [end-index (.end m)
                  start-index (.start m)]
              (recur  end-index
                      (.find m)
                      [(into  (first result) [substr `(array-mark ~(symbol v))])
                       `(into ~(second result) ~(symbol v))]))))
        (let [tail-str (.substring sql-str index (count sql-str))]
          [(conj (first result) tail-str) (second result)])))))



(defmacro sql-reader [sql-str]
  (let [[headers params] (build-sql-construct sql-str)]
    `(into [(apply str ~headers)] ~params)))


(defn merge-sql [sqls]
  (into [(clojure.string/join " " (map #(first %) sqls))] (reduce concat  (map #(next %) sqls))))

(defmacro sql-readers [& sql-strs]
  `(merge-sql [~@(map #(prewalk (fn [item]
                                  (if (and (seq? item) (= 2 (count item)) (= (first item) 'clojure.core/deref)
                                           (string? (second item)))
                                    `(sql-reader ~(second item))
                                    item))
                                %)
                      sql-strs)]))

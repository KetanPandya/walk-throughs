(ns walk-throughs.core
  (:gen-class)
  (:require [clojure.data.csv :as csv]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment
  Bindings  We saw three levels of bindings in Clojure.
  Global, Local and Function level bindings.)

(def global-binding 1)

(let [local-binding 2]
  (println "Local binding is " local-binding)
  (println "Global binding is " global-binding))

(defn function-binding [function-arg])
  
(comment
  Flow Controls in Clojure
  1. if
  2. when
  3. cond
  4. case
  5. loop
  6. recur
  7. for
  8. doseq
  9. dotimes
  10. while
  11. when-let
  12. if-let
  13. cond->, cond->>
  14. some->, some->>
  15. as->
  16. condp
  17. and
  18. or
  19. not

 ;;example of if in clojure
  (if (= 2 2)
   (println "This is true")
   (println "This is false")))

;;example of when in clojure
(when true
  (println "This is true"))

;;example of cond in clojure
(cond
  (= 1 1) (println "This is true")
  (= 1 2) (println "This is false")
  :default "Hello")

;;example of case in clojure 
(case 1
  1 (println "This is true")
  2 (println "This is false"))

;;example of loop in clojure
(loop [x 0 y 0]
  (when (< x 10)
    (println x)
    (recur (inc x) (inc y))))

;;example of for in clojure
(for [x (range 10)]
  (prn x))

;;  a complex example of for in clojure
(for [x (range 10)
      y (range 10)
      :when (even? x)]
  (println x y))

;;example of doseq in clojure
(range 1 10 2)

(doseq [x (range 10)]
  (println x))

;;example of dotimes in clojure
(dotimes [x 10]
  (println x))

;;example of while in clojure
(let [x (atom 0)]
  (while (< @x 10)
    (println @x)
    (swap! x inc)))

;; examples of  the map function in clojure
(map inc [1 2 3 4 5])

(defn add-one [n] (+ n 1))
(map add-one [1 2 3 4 5])

(map #(+ % 1) [1 2 3 4 5])

;; example of the filter function in clojure
(filter even? [1 2 3 4 5])
;;example  of a filter which takes a map and returns a map
(filter #(> (val %) 2) {:a 1 :b 2 :c 3})

;; examples of the reduce function in clojure
(reduce + [1 2 3 4 5])

(reduce + 5 [1 2 3 4 5])

;; example of a reduce which take a map and conj the key value pair and applies the function to the value
(reduce (fn [m [k v]] (conj m [k (inc v)])) [] {:a 1 :b 2 :c 3})
(reduce (fn [m [k v]] (conj m [k (inc v)])) {} [[:a 1] [:b 2] [:c 3] [:c 4]])

(reduce (fn [m [k v]] (conj m [k (inc v)])) {} {:a 1 :b 2 :c 3})



;;we have some y rears of time series stock data called stock-data.csv
;; we read in the csv file of stock data and convert it to a vector 
;; of maps using clojure.data.csv

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn read-csv [file]
  (with-open [reader (clojure.java.io/reader file)]
    (csv-data->maps (doall (csv/read-csv reader)))))

(def msft-prices-file  "./resources/msft.csv")

(def msft-price-data (read-csv msft-prices-file))

(println (take 2 msft-price-data))

(defn price-diff [t t-1]
  (- (Double/parseDouble (:Close t)) (Double/parseDouble (:Close t-1))))

(defn price-diffs [price-data]
  (map price-diff price-data (rest price-data)))

(defn daily-changes [t t-1]
  (let [price-diff (price-diff t t-1)]
    (conj t {:price-diff price-diff})))

(defn daily-process [price-data process-fn]
  (map process-fn price-data (rest price-data)))

(daily-process msft-price-data daily-changes)

(defn daily-changes [t t-1]
  (let [price-diff (price-diff t t-1)
        daily-return (/ price-diff (Double/parseDouble (:Close t-1)))
        log-return (Math/log (/ (Double/parseDouble (:Close t)) (Double/parseDouble (:Close t-1))))]
    (conj t {:price-diff price-diff :daily-return daily-return :log-return log-return})))

(take 1 (daily-process msft-price-data daily-changes))


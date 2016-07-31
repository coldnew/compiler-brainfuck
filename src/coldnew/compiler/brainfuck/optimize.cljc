(ns coldnew.compiler.brainfuck.optimize
  (:require #?(:clj  [clojure.pprint :refer [cl-format]]
               :cljs [cljs.pprint :refer [cl-format]])))

(declare reduce-consecutive-terms reduce-empty-loops reduce-common-idiom)

(defn optimize
  ([ir] (optimize ir 0))
  ([ir optimize-level]
   (-> ir
       ;; Reduce empty loops for every optimize level to prevent generate code error
       reduce-empty-loops
       ;; Reduce common idiom only aviable for optimize level 2 or above
       ((fn [x] (if-not (>= optimize-level 2)
                  x
                  (reduce-common-idiom x))))

       ;; Reduce consecutive terms only aviable for optimize level 1 or above
       ((fn [x] (if-not (>= optimize-level 1)
                  x
                  (reduce-consecutive-terms x))))
       )))

(defn reduce-empty-loops
  "Loops that contains empty children are safely removed."
  [ir]
  (filter (fn [s]
            (case (:op s)
              :loop (if (empty? (:children  s))
                      false
                      true)
              true))
          ir))

;;;; Reduce consecutive terms

(defn partition-by-type [ir]
  (partition-by
   (fn [x]
     (case (:op x)
       (:add :sub)    1
       (:right :left) 2
       :input         3
       :output        4
       :loop          5
       ;; else
       100))
   ir))

(defn reduce-set-cell-value [s indent-depth]
  (loop [val 0
         s s]
    (if (empty? s)
      {:op :set-cell-value
       :val val
       :indent-depth indent-depth
       :comment "Increment/Decrement multiple bytes at the data pointer according to :val."}
      (recur (+ val (case (:op (first s))
                      :add 1
                      :sub -1
                      (throw (ex-info "Unknown opcode" {:code "1"}))))
             (rest s)))))

(defn reduce-set-cell-pointer [s indent-depth]
  (loop [val 0
         s s]
    (if (empty? s)
      {:op :set-cell-pointer
       :val val
       :indent-depth indent-depth
       :comment "Increment/Decrement the data pointer according to :val."}
      (recur (+ val (case (:op (first s))
                      :right 1
                      :left -1
                      (throw (ex-info "Unknown opcode" {:code "1"}))))
             (rest s)))))

(defn reduce-consecutive-terms-impl
  [s]
  (case (:op (first s))
    (:add :sub)    (reduce-set-cell-value   s (:indent-depth (first s)))
    (:right :left) (reduce-set-cell-pointer s (:indent-depth (first s)))
    :loop {:op :loop
           :form (:form (first s))
           :indent-depth (:indent-depth (first s))
           :comment      (:comment (first s))
           :children (map reduce-consecutive-terms-impl
                          (partition-by-type (:children (first s))))}
    (:input :output) (first s)          ;TODO
    ;; else just return
    (first s)))

(defn reduce-consecutive-terms
  [ir]
  (->> ir
       partition-by-type
       (map reduce-consecutive-terms-impl)))

;;;; Reduce common idiom
;; http://calmerthanyouare.org/2015/01/07/optimizing-brainfuck.html

(defn reduce-common-idiom-impl
  [s]
  (case (:op s)
    :loop (case (:form s)
            ("[-]" "[+]")    (assoc-in s [:children]
                                       [{:op :clear
                                         :form (:form s)
                                         :indent-depth (inc (:indent-depth s))
                                         :comment "clear loops"}])
            ;; else
            (assoc-in s [:children]
                      (map reduce-common-idiom-impl (:children s))))
    ;; not :loop condition
    s))

(defn reduce-common-idiom [ir]
  (->> ir
       (map reduce-common-idiom-impl)))

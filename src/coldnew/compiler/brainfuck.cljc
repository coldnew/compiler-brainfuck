(ns coldnew.compiler.brainfuck
  (:require [coldnew.compiler.brainfuck.compiler :refer [compile-to]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            #?(:cljs [cljs.nodejs :as nodejs]))
  (:refer-clojure :exclude [compile])
  #?(:clj (:gen-class)))

(defn compile
  [{:keys [source-code filename target optimize] :as options
    :or {optimize 0}}]
  (compile-to options))

;; A way to convert str->int for both clojure/clojurescript
(defn- str->int
  [s]
  (when (and (string? s)
             (re-find #"^\d+$" s))
    (read-string s)))

(def ^:private cli-options
  ;; An option with a required argument
  [["-i" "--input-file  INPUT"]
   ["-o" "--output-file OUTPUT"]
   ["-t" "--target LANGUAGE"      "Target language"
    :parse-fn #(keyword %)
    :validate [#(contains? #{:c :java :nodejs :python :rust} %) "Unsupported target"]]
   ["-O" "--optimize-level LEVEL" "Optimize level"
    :default 0
    :parse-fn #(str->int %)
    :validate [#(< 0 % 3) "Must be a number in 0 ~ 2 (inclusive)."]]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn- usage [options-summary]
  (->> [""
        "Usage: program-name [options]"
        ""
        "Options:"
        options-summary
        ""
        "Optimize level:"
        "  0     No optimize. (default)"
        "  1     Remove consecutive terms."
        "  2     Remove common idiom."
        ""
        "Target language:"
        "  c"
        "  java"
        "  nodejs"
        "  python"
        "  rust"
        ""]
       (str/join \newline)))

(defn- error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn- exit [status msg]
  (println msg)
  #?(:clj (System/exit status)
     :cljs
     (.exit nodejs/process status)))

;; enable *print-fn* in clojurescript
#?(:cljs (enable-console-print!))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      ;; (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    (println "Input : " (:input-file options))
    (println "Output: " (:output-file options))
    (println "Target: " (name (:target options)))
    (println "Optimize Level: " (:optimize-level options))
    ;; TODO: check file exists
    ;; TODO: save generate string to file
    (spit (:output-file options)
          (compile {:source-code (slurp (:input-file options))
                    :target      (:target     options)
                    :optimize    (:optimize-level options)}
                   ))
    )
  )

;; setup node.js starter point
#?(:cljs (set! *main-cli-fn* -main))
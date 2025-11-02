(ns build
  (:require
   [clojure.tools.build.api :as b]))

(defn issue [opts]
  (println "Creating basis...")
  (try
    (b/create-basis {:project "deps.edn"})
    (catch Exception e
      (println :exception e)
      (System/exit 32)
      ;;(Thread/sleep 6000000000)
      )))


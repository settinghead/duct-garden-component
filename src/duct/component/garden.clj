(ns duct.component.garden
  "A component for running Garden CSS compiler."
  (:require [com.stuartsierra.component :as component]
            [suspendable.core :as suspendable]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]))

(defn- builds [project]
  (-> project :garden :builds))

(defn- output-path [build]
  (-> build :compiler :output-to))

(defn- validate-builds [project]
  (doseq [{:keys [id stylesheet source-paths] :as build} (builds project)]
    (cond
     (nil? source-paths)
     (throw (Exception. (format "No source-paths specified in build %s. " (name id))))
     (nil? stylesheet)
     (throw (Exception. (format "No stylesheet specified in build %s. " (name id))))
     (not (symbol? stylesheet))
     (throw (Exception. (format ":stylesheet value must be a symbol in build %s." (name id))))
     (nil? (output-path build))
     (throw (Exception. (format "No :output-to file specified in build %s." (name id)))))))

(defn- ensure-output-directory-exists [build]
 (let [dir (-> (output-path build)
               io/file
               fs/absolute-path
               fs/parent)]
   (when-not (fs/exists? dir)
     (when-not (fs/mkdirs dir)
       (throw (Exception. (format "Could not create directory %s" dir)))))))

(defrecord Server [builds]
  component/Lifecycle
  (start [component]
    (println "Starting Garden...")
    (assoc component :a "b"))
  (stop [component]
    (println "Stopping Garden...")
    (assoc component :a "b"))
  suspendable/Suspendable
  (suspend [component]
    (println "Pausing Garden...")
    (assoc component :a "b"))
  (resume [component old-component]
    (println "Resuming Garden...")
    (assoc component :a "b")))

(defn server [options]
  (map->Server options))

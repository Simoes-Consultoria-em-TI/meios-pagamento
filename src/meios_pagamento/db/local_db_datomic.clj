(ns meios-pagamento.db.local-db-datomic
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [datomic.client.api :as d]
            [clojure.tools.logging :as log]))

(def cfg
  {:server-type :datomic-local
   :system "dev"
   :storage-dir :mem ;;"data/datomic"  ;; persiste em disco (recomendado)
   :db-name "bank"})

(defonce client* (atom nil))
(defonce conn*   (atom nil))

(defn client []
  (or @client* (reset! client* (d/client cfg))))

(defn ensure-db! []
  (let [c (client)]
    (when-not (d/list-databases c {})
      ;; list-databases existe no client api; mas ele lista tudo, então:
      ;; vamos verificar se "bank" já está na lista
      nil)
    (let [dbs (set (d/list-databases c {}))]
      (when-not (contains? dbs (:db-name cfg))
        (d/create-database c {:db-name (:db-name cfg)})))))

(defn connect! []
  (ensure-db!)
  (reset! conn* (d/connect (client) {:db-name (:db-name cfg)}))
  @conn*)

(defn conn []
  (or @conn* (connect!)))

(defn db []
  (d/db (conn)))

(defn load-schema! [file]
  (let [schema (-> file io/resource slurp edn/read-string)]
    (d/transact (conn) {:tx-data schema})))

(defn apply-all-schemas! []
  (log/info "Loading schedule schema")
  (load-schema! "schedule-schema.edn")
  (log/info "Loading transaction schema")
  (load-schema! "transaction-schema.edn"))
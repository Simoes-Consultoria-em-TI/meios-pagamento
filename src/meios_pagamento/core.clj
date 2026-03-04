(ns meios-pagamento.core
  (:gen-class)
  (:require [datomic.client.api :as d]
            [meios-pagamento.db.local-db-datomic :as db]))


(defn start! []
  (db/connect!)
  (db/apply-all-schemas!))
  (println "Datomic is up. DB name:" (:db-name db/cfg))

(defn -main [& _args]
  (start!))
  ;; aqui você levanta o resto da aplicação (http server etc)



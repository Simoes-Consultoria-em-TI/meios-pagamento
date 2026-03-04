(ns meios-pagamento.db.repository.schedule-repository
  (:require [datomic.client.api :as d]
            [meios-pagamento.db.local-db-datomic :as db])
  (:import (java.time ZoneId ZonedDateTime)
           (java.util Date)))


(defn find-scheduled-transactions! []
  ;;find all transactions scheduled for today
  (-> (d/q '[:find (pull ?s [*])
             :in $ ?now
             :where [?s :schedule/process-at ?p]
             [(<= ?p ?now)]
             [?s :schedule/status :schedule.status/pending]] (db/db)
           (Date/from (.toInstant (ZonedDateTime/now (ZoneId/of "UTC")))))
      ffirst))
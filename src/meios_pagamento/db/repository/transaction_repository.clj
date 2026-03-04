(ns meios-pagamento.db.repository.transaction-repository
  (:require [clojure.tools.logging :as log]
            [datomic.client.api :as d]
            [meios-pagamento.db.local-db-datomic :as db]
            [meios-pagamento.util.util :as u]))

(defn- ->transaction [{:transaction/keys [id
                                          due-date
                                          amount
                                          type
                                          status
                                          correlation-id
                                          date-fail] :as transaction-entity}]
  (log/info "Starting converting to transaction" transaction-entity)
  (let [base {:transaction-id id
              :due-date       (u/instant->localdate due-date)
              :amount         amount
              :type           type
              :status         status
              :correlation-id correlation-id}
        entity (cond-> base
                       (some? date-fail)
                       (assoc :date-fail date-fail))]
    entity))

(defn find-by-correlation-id! [correlation-id]
  (log/info "Finding transaction" correlation-id)
  (if-let
    [found (-> (d/q '[:find (pull ?t [*])
                      :in $ ?cid
                      :where [?t :transaction/correlation-id ?cid]]
                    (db/db)
                    (u/->uuid correlation-id))
               ffirst)]
    (->transaction found)
    nil))




(defn- ->transaction-entity [{:keys [due-date amount type correlation-id date-fail] :as transaction}]
  (log/info "Starting converting to db entity")
  (let [tx-id (random-uuid)
        base {:transaction/id             tx-id
              :transaction/due-date       (u/->instant due-date)
              :transaction/amount         (u/->bigdec amount)
              :transaction/type           (keyword type)    ;; ex: \"transaction.type/pix\"
              :transaction/status         (keyword :start)  ;; ex: \"transaction.status/pending\"
              :transaction/correlation-id (u/->uuid correlation-id)}
        entity (cond-> base
                       (some? date-fail)
                       (assoc :transaction/date-fail (u/->instant date-fail)))]
    entity))



(defn save-transaction!
  "Recebe um mapa de entrada e salva uma transaction no Datomic.
   Retorna o UUID da transaction criada."
  [transaction]
  (let [entity (->transaction-entity transaction)
        tx-res (d/transact (db/conn) {:tx-data [entity]})
        db-after (:db-after tx-res)
        eid [:transaction/id (:transaction/id entity)]
        saved (d/pull db-after
                      '[:transaction/id
                        :transaction/due-date
                        :transaction/amount
                        :transaction/type
                        :transaction/status
                        :transaction/correlation-id
                        :transaction/date-fail]
                      eid)
        mapped (->transaction saved)]
    (log/info "Transaction mapped" mapped)
    mapped))




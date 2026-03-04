(ns meios-pagamento.domain.payments.schedule.use-case.payment-workflow.transaction-workflow
  (:require
    [meios-pagamento.domain.payments.schedule.entity.pix-payment :as pp]
    [meios-pagamento.db.repository.transaction-repository :as tr]
    [clojure.tools.logging :as log]))

(def workflow
  {:pix pp/steps})

(defn- ->kw [v]
  (cond
    (keyword? v) v
    (string? v) (keyword v)
    :else v))

(defn- transaction-workflow [transaction workflows]
  (loop [tx transaction]
    (log/info "Transaction")
    (let [status (->kw (:status tx))]
      (log/info "Verifying status" status)
      (if (= status :finished)                      ;; ou :transaction.status/finished (veja abaixo)
        (do
          (log/info "Finished " (:type tx) "with status" status)
          tx)
        (do
          (log/info "Workflows " workflows)
          (let [step-fn (status workflows)]
            (when-not step-fn
              (throw (ex-info "Não existe step para esse status"
                              {:status    status
                               :available (keys workflows)
                               :tx        tx})))
            (recur (step-fn tx))))))))

(defn find-transaction-into-database [{:keys [correlation-id] :as transaction}]
  (if-let [transaction-found (tr/find-by-correlation-id! correlation-id)]
    (do
      (log/info "Transaction found" transaction-found)
      transaction-found)
    (do
      (log/info "Transaction not found, starting save new transaction")
      (tr/save-transaction! transaction))))


(defn workflow-start [{:keys [type] :as transaction}]
  (let [transaction-found (find-transaction-into-database transaction)]
    (cond (not (= nil transaction-found))
          (do (log/info "Workflow map" type (get workflow type))
              (transaction-workflow transaction-found (get workflow type)))
          :else (transaction-workflow transaction (get workflow type)))))

(workflow-start {:due-date "2026-03-10"
                 :amount "1000.00"
                 :type :pix
                 :status :start
                 :correlation-id "a71240a5-3c7e-4b13-9d1d-8bae517234c4"})
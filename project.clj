(defproject meios-pagamento "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [org.clojure/tools.logging "1.2.4"]
                 [ch.qos.logback/logback-classic "1.4.14"]
                 [com.datomic/local "1.0.276"]]
  :main meios-pagamento.core
  :aot [meios-pagamento.core]
  :repl-options {:init-ns meios-pagamento.core})

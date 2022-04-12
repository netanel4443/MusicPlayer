package com.e.musicplayer.data.localdb.utils

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import java.lang.invoke.CallSite
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun <T:RealmObject> Realm.save(
        clazz: Class<T>, id: String, who: String, block: () -> RealmObject
    ): Single<String> {
      return  Single.create { emitter->
          try {
              executeTransactionAsync {
                  val rlmObj = where(clazz).equalTo(id, who).findFirst()

                  if (rlmObj != null) {
                        emitter.onSuccess("Already saved")
                      return@executeTransactionAsync
                  }
                  insertOrUpdate(block())
                  emitter.onSuccess("Saved")
              }
          } catch (e: Exception) {
                emitter.onError(e)
          }
        }
    }

    fun <T:RealmObject> Realm.deleteItem(clazz:Class<T>,id:String,who: String):Completable {

        return Completable.create { emitter ->
            try {
                executeTransactionAsync {
                   where(clazz).equalTo(id, who).findFirst()?.deleteFromRealm()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun <T:RealmObject,K,V> Realm.getItems(clazz:Class<T>,block:(RealmResults<T>)->HashMap<K,V>):Single<HashMap<K,V>>{
        return Single.create { emitter ->
            try {
                val rlmResults = where(clazz).findAll()
                emitter.onSuccess(block(rlmResults))
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

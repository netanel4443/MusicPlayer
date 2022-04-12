package com.e.musicplayer.data.localdb.utils

import io.reactivex.rxjava3.core.Single
import io.realm.*

fun <T:RealmObject ,V:RealmObject> Realm.saveItemToList(
    clazz: Class<V>, id: String, who: String,block: (Realm,V) ->Pair<T,RealmList<T>>
): Single<String> {
    return  Single.create { emitter->
        try {
            executeTransactionAsync {realm->

              var rlmObj= realm.where(clazz).equalTo(id,who).findFirst()
                if (rlmObj==null){
                    rlmObj= realm.createObject(clazz,who)
                }

                val pair=block(realm,rlmObj!!)
                val list=pair.second
                val objectToSave=pair.first


                if (list.contains(objectToSave)) {

                    emitter.onSuccess("Already saved")
                    return@executeTransactionAsync
                }

               list.add(objectToSave)

                realm.insertOrUpdate(rlmObj)
                emitter.onSuccess("Saved")
            }
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}
/** Returns the desired [RealmObject], if is not existed , create it.
 * Should be wrapped with try & catch .
 * Use with [Realm.executeTransactionAsync] block
 */
//fun <T:RealmObject> getRlmObj(clazz: Class<T>, id:String, who: String,realm: Realm):T{
//
//            var rlmObj= realm.where(clazz).equalTo(id,who).findFirst()
//            if (rlmObj==null){
//                rlmObj= realm.createObject(clazz,who)
//            }
//    return rlmObj!!
//}

fun <T:RealmObject,V:RealmObject> Realm.deleteItemFromList(
    clazz:Class<V>,
    id:String,
    who: String,
    block:(V)->T ) :Single<String> {

    return Single.create { emitter ->
        try {
            executeTransactionAsync { realm->
                val objWithList=realm.where(clazz).equalTo(id, who).findFirst()
                println(objWithList)
                val objectToDelete= block(objWithList!!)

                    (objectToDelete).deleteFromRealm()
                    emitter.onSuccess("Item deleted")

            }
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}

fun <T : RealmObject, K, V> Realm.getLists(
    clazz: Class<T>,
    block: (RealmResults<T>) -> HashMap<K, V>
): Single<HashMap<K, V>> {
    return Single.create { emitter ->
        try {
            val listener = object : RealmChangeListener<RealmResults<T>> {
                override fun onChange(results: RealmResults<T>) {
                    if (results.isLoaded) {
                        emitter.onSuccess(block(results))
                        results.removeChangeListener(this)
                    }
                }
            }
            where(clazz).findAllAsync().addChangeListener(listener)
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}

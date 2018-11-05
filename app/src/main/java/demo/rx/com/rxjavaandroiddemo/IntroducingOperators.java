package demo.rx.com.rxjavaandroiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
    * You can see Operators introduced in this example
    * Now we will see another example by introducing an operator to transform the emitted data.
    * In the below example filter() operator is used to filter out the emitted data.
 **/
public class IntroducingOperators extends AppCompatActivity {
    /**
     * Basic Observable, Observer, Subscriber example
     * Observable emits list of students names
     * Disposable is used to dispose the subscription when an Observer no longer wants to listen to Observable.
     * In android disposable are very useful in avoiding memory leaks.
     */

    private static final String TAG = IntroducingOperators.class.getSimpleName();

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Observable: class that emits a stream of data or events.
        // i.e. a class that can be used to perform some action, and publish the result.
        Observable<String> studentObservable = getstudentObservable();

        //Observer: class that receivers the events or data and acts upon it.
        // i.e. a class that waits and watches the Observable, and reacts whenever the Observable publishes results.
        Observer<String> studentObserver = getStudentObserver();

        // observer subscribing to observable
        // Make Observer subscribe to Observable so that it can start receiving the data. Here, you can notice two more methods, observeOn() and subscribeOn().
        //subscribeOn(Schedulers.io()): This tell the Observable to run the task on a background thread.
        //observeOn(AndroidSchedulers.mainThread()): This tells the Observer to receive the data on android UI thread so that you can take any UI related actions.
        studentObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //filter() operator filters the data by applying a conditional statement.
                //The data which meets the condition will be emitted and the remaining will be ignored
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.toLowerCase().startsWith("m");
                    }
                })
                .subscribeWith(studentObserver);
    }

    /**
     * The Observer has 4 interface methods to know the different states of the Observable.
     *onSubscribe(): This method is invoked when the Observer is subscribed to the Observable.
     *onNext(): This method is called when a new item is emitted from the Observable.
     *onError(): This method is called when an error occurs and the emission of data is not successfully completed.
     *onComplete(): This method is called when the Observable has successfully completed emitting all items.
     */
    private Observer<String> getStudentObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable=d;
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Toast.makeText(IntroducingOperators.this,"Name: " + s,Toast.LENGTH_LONG).show();
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    /**
         * From Operator Introduction
         * This operator creates an Observable from set of items using an Iterable,
         * which means we can pass a list or an array of items to the Observable and each item is emitted one at a time.
         * Some of the examples of the operators include
         * fromCallable(), fromFuture(), fromIterable(), fromPublisher(), fromArray().
     **/
    private Observable<String> getstudentObservable() {
        return Observable.fromArray("Manish", "John", "Kate", "Ashish", "Meghan");
    }


    /**
     * Benefits of Disposable
     * Let’s say you are making a long running network call and updating the UI.
     * By the time network call completes its work, if the activity / fragment is already destroyed,
     * as the Observer subscription is still alive, it tries to update already destroyed activity.
     * In this case it can throw a memory leak. So using the Disposables,
     * the un-subscription can be when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send events once the activity is destroyed
        disposable.dispose();
    }
}

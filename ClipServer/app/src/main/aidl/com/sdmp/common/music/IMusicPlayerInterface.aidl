// IMusicPlayerInterface.aidl
package com.sdmp.common.music;
import com.sdmp.common.music.callback;
// Declare any non-default types here with import statements

interface IMusicPlayerInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    oneway void play(int song,in callback cb);

    void pause();

    void resume();

    void stopAndUnbind();

    //oneway void setCallBack(in callback cb);
}

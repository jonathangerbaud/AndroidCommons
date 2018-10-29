package fr.jonathangerbaud.uimanager;

import androidx.lifecycle.MutableLiveData;
import fr.jonathangerbaud.network.Resource;

public abstract class DataFactory<T>
{
    public abstract MutableLiveData<Resource<T>> build();
}

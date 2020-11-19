package org.metis.fx.workbench;

import java.util.function.Consumer;

import javafx.event.ActionEvent;

public abstract class EditingDialogController<T> extends DialogController{

	public abstract void bindData( T data, Consumer<T> saveconsumer);	
	protected abstract void doSave();
	
	
	
	
	public void commit(ActionEvent event) {
    	doSave();
		close();
	}
	

    public void cancel(ActionEvent event) {
		close();
	}
}

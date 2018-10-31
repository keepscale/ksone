package org.crossfit.app.web.rest.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class PaginateList<E>
        implements Iterable<E> {
    
    
    private List<E> results;
    
    private int length;
    private int pageIndex;
    private int pageSize;
    
    public PaginateList() {
        this(-1, -1);
    }
    
    public PaginateList(final int pageIndex, final int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }
    
    public List<E> getResults() {
        return results;
    }
    
    public int getPageSize() {
        return (0 < pageSize && pageSize < length) ? pageSize : length;
    }    
    
    public int getPageIndex() {
        return pageIndex <= -1 || pageIndex > getLastPage() ? 1 : pageIndex;
    }
    
    public int getLength() {
    	return this.length;
    }

    private int getStartIndex() {
        int startIndex = (getPageSize() * getPageIndex());
        return (startIndex < 0 || startIndex > this.length) ? 0 : startIndex;
    }
    
    private int getEndIndex() {
        int endIndex = getPageSize() * (getPageIndex()+1);
        return (endIndex < 0 || endIndex > this.length || endIndex < getStartIndex()) ? this.length : endIndex;
    }
    
    private int getLastPage() {
        if (this.length == 0) {
            return 1;
        }
        
        return this.length / getPageSize()
                + (this.length % getPageSize() > 0 ? 1 : 0);
    }
    
    
    public PaginateList<E> paginate(final Collection<E> list) {
        ArrayList<E> allResults = new ArrayList<E>(list);
		this.length = allResults.size();
		
		List<E> subList = allResults.subList(this.getStartIndex(), this.getEndIndex());
        this.results = subList.isEmpty() ? allResults : subList;
        
        return this;
    }
    
    @Override
    public Iterator<E> iterator() {
        return getResults().iterator();
    }
    
    @Override
    public void forEach(final Consumer<? super E> c) {
        getResults().forEach(c);
    }
    
    @Override
    public Spliterator<E> spliterator() {
        return getResults().spliterator();
    }
}

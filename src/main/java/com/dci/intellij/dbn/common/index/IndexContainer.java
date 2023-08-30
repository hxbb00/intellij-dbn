package com.dci.intellij.dbn.common.index;

import com.dci.intellij.dbn.common.util.Compactable;
import com.dci.intellij.dbn.common.util.Unsafe;
import org.jetbrains.coverage.gnu.trove.TIntHashSet;
import org.jetbrains.coverage.gnu.trove.TIntIterator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class IndexContainer<T extends Indexable> implements Compactable {
    private final TIntHashSet INDEX = new TIntHashSet();

    public void add(T element) {
        INDEX.add(element.index());
    }

    public boolean isEmpty() {
        return INDEX.isEmpty();
    }

    public boolean contains(T indexable) {
        // TODO workaround - IOOBE, NPE happens in parser lookup caches (probably due to latent background initialization)
        return Unsafe.silent(false, indexable, p -> INDEX.contains(p.index()));
    }

    public Set<T> elements(Function<Integer, T> resolver) {
        if (INDEX.isEmpty()) {
            return Collections.emptySet();
        } else {
            Set<T> elements = new HashSet<>(INDEX.size());
            TIntIterator iterator = INDEX.iterator();
            while (iterator.hasNext()) {
                int next = iterator.next();
                T element = resolver.apply(next);
                if (element != null) {
                    elements.add(element);
                }
            }
            return elements;
        }
    }

    @Override
    public void compact() {
        INDEX.trimToSize();
    }

    public void addAll(Collection<T> elements) {
        for (T element : elements) {
            INDEX.add(element.index());
        }
    }
}

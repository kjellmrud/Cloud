package no.ic.cloud.blog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBlogStore implements BlogStore {

    private final Object lock = new Object();
    private final Map<String, List<String>> entries;

    public InMemoryBlogStore() {
        entries = new HashMap<String, List<String>>();
    }

    public List<String> getThreads() {
        synchronized (lock) {
            return new ArrayList<String>(entries.keySet());
        }
    }

    public void addThread(String name) {
        synchronized (lock) {
            createThread(name);
        }
    }

    public List<String> getPosts(String thread) {
        synchronized (lock) {
            List<String> posts = entries.get(thread);
            if (posts == null) {
                throw new IllegalStateException("The thread '" + thread + "' does not exist");
            }
            return new ArrayList<String>(posts);
        }
    }

    public void addPost(String thread, String post) {
        synchronized (lock) {
            List<String> posts = entries.get(thread);
            if (posts == null) {
                throw new IllegalStateException("The thread '" + thread + "' does not exist");
            }
            posts.add(post);
        }
    }

    private List<String> createThread(String name) {
        if (entries.containsKey(name)) {
            return entries.get(name);
        }
        List<String> thread = new ArrayList<String>();
        entries.put(name, thread);
        return thread;
    }

}

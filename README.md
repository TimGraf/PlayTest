# Graceful Shutdown Filter for Play#

This is based on the following topic.

[Play framework GiHub Issue: 1789](https://github.com/playframework/playframework/issues/1789)

<br />
<br />

In the filter, [ShutdownFilter](app/filters/ShutDownFilter.scala) , the approach from the link above was modified to use `StampedLock` instead of `ReentrantReadWriteLock` because the call to `lock.unlock()` used in `body.onDoneEnumerating()` occurs on another thread and will throw the following.

`java.lang.IllegalMonitorStateException  "attempt to unlock read lock, not locked by current thread"`

See also:

[StampedLock](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/StampedLock.html)

[ReentrantReadWriteLock](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html)

<br />
<br />

*Build and run using the following commands.*

sbt clean stage

target/universal/stage/bin/PlayTest
//
// Created by victor on 24.12.17.
//

#ifndef QUEUE_H
#define QUEUE_H

#include <memory>
#include <mutex>
#include <queue>
#include <condition_variable>

template<typename T>
class ThreadQueue {
private:
    mutable std::mutex mutex;
    std::queue<T> queue;
    std::condition_variable data;
public:
    ThreadQueue() {}

    ThreadQueue(ThreadQueue const &other) {
        std::lock_guard<std::mutex> locker(other.mutex);
        std::copy(std::begin(other.queue), std::end(other.queue), std::begin(queue));
    }

    void push(T value) {
        std::lock_guard<std::mutex> locker(mutex);
        queue.push(value);
        data.notify_one();
    }

    void wait_and_pop(T &value) {
        std::unique_lock<std::mutex> locker(mutex);
        data.wait(locker, [this] { return !queue.empty(); });
        value = queue.front();
        queue.pop();
    }

    std::__shared_ptr<T> wait_and_pop() {
        std::unique_lock<std::mutex> locker(mutex);
        data.wait(locker, [this] { return !queue.empty(); });
        std::shared_ptr<T> result(std::make_shared<T>(queue.front()));
        queue.pop();
        return result;
    }

    bool try_pop(T &value) {
        std::lock_guard<std::mutex> locker(mutex);
        if (queue.empty()) return false;
        value = queue.front();
        queue.pop();
        return true;
    }

    std::shared_ptr<T> try_pop() {
        std::lock_guard<std::mutex> locker(mutex);
        if (queue.empty()) return std::shared_ptr<T>();
        std::shared_ptr<T> result(std::make_shared<T>(queue.front()));
        queue.pop();
        return result;
    }

    bool empty() {
        return queue.empty();
    }
};


#endif //QUEUE_H


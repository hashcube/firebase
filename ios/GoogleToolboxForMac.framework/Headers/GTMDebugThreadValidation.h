#import "GTMDefines.h"
#import <Foundation/Foundation.h>

// GTMCheckCurrentQueue, GTMIsCurrentQueue
//
// GTMCheckCurrentQueue takes a target queue and uses _GTMDevAssert to
// report if that is not the currently executing queue.
//
// GTMIsCurrentQueue takes a target queue and returns true if the target queue
// is the currently executing dispatch queue. This can be passed to another
// assertion call in debug builds; it should never be used in release code.
//
// The dispatch queue must have a label.
#define GTMCheckCurrentQueue(targetQueue)                    \
  _GTMDevAssert(GTMIsCurrentQueue(targetQueue),              \
                @"Current queue is %s (expected %s)",        \
                _GTMQueueName(DISPATCH_CURRENT_QUEUE_LABEL), \
                _GTMQueueName(targetQueue))

#define GTMIsCurrentQueue(targetQueue)                 \
  (strcmp(_GTMQueueName(DISPATCH_CURRENT_QUEUE_LABEL), \
          _GTMQueueName(targetQueue)) == 0)

#define _GTMQueueName(queue)                     \
  (strlen(dispatch_queue_get_label(queue)) > 0 ? \
    dispatch_queue_get_label(queue) : "unnamed")

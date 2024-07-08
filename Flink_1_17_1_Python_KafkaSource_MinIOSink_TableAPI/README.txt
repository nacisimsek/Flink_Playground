This Pyflink Job written in TableAPI has been executed on a Session cluster deployed with the custom Flink image created by the dockerfile in this folder.
The execution of the flink job is done by copying the python file inside the cluster and run below commands:

docker cp my_flink_job.py flink-jobmanager:/opt/flink/my_flink_job.py
docker exec -it flink-jobmanager /bin/bash
docker logs -f flink-taskmanager
docker logs -f flink-jobmanager
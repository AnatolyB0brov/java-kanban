package manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault();
    }

}
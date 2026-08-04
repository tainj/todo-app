ALTER TABLE tasks DROP CONSTRAINT tasks_category_id_fkey;
ALTER TABLE tasks ADD CONSTRAINT tasks_category_id_fkey
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;
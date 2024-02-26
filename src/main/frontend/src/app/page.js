"use client";
import debounce from "lodash.debounce";
import { useEffect, useState } from "react";
import { Flip, ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import styles from "./page.module.css";

export default function Home() {
  const [tasks, setTasks] = useState([]);
  const [filter, setFilter] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState();

  const [taskUpdate, setTaskUpdate] = useState();
  const [newTaskTitle, setNewTaskTitle] = useState("");
  const [newTaskDesc, setNewTaskDesc] = useState("");

  const maxItensPerPage = 4;
  const baseUrl = "http://localhost:8080";

  const count = (filter, pageNumber, pageSize) => {
    fetch(`${baseUrl}/todo/count?filter=${filter}`, {
      // mode: 'no-cors',
      method: "GET",
    }).then((response) => {
      if (response.ok) {
        response.text().then(function (text) {
          setTotalPages(Math.ceil(+text / maxItensPerPage));
        });
      }
    });
  };

  const updateList = async (filter, pageNumber, pageSize) => {
    await count(filter);
    fetch(
      `${baseUrl}/todo/list?filter=${filter}&pageNumber=${
        pageNumber - 1
      }&pageSize=${pageSize}`,
      {
        // mode: 'no-cors',
        method: "GET",
        headers: {
          Accept: "application/json",
        },
      }
    ).then((response) => {
      if (response.ok) {
        response.json().then((json) => {
          setTasks(json);
        });
      }
    });
  };

  const genericUpdateList = () => {
    updateList(filter, currentPage, maxItensPerPage);
  };

  useEffect(() => {
    genericUpdateList();
  }, []);

  useEffect(() => {
    genericUpdateList();
  }, [currentPage, filter]);

  const handleChangeFilter = (event) => {
    const { value: nextValue } = event.target;
    const debouncedSave = debounce(() => {
      setFilter(nextValue);
      setCurrentPage(1);
    }, 1000);
    debouncedSave();
  };

  const addNewItem = () => {
    fetch(`${baseUrl}/todo/create`, {
      // mode: 'no-cors',
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        title: newTaskTitle,
        description: newTaskDesc,
      }),
    }).then((response) => {
      if (response.ok) {
        response.json().then((json) => {
          toast.success("New item added!");
          setNewTaskTitle("");
          setNewTaskDesc("");
          setTaskUpdate();
          genericUpdateList();
        });
      }
    });
  };

  const setToUpdate = (task) => {
    setTaskUpdate(task);
    setNewTaskTitle(task.title);
    setNewTaskDesc(task.description || "");
  };

  const updateItem = () => {
    fetch(`${baseUrl}/todo/update/${taskUpdate.id}`, {
      // mode: 'no-cors',
      method: "PATCH",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        title: newTaskTitle,
        description: newTaskDesc,
      }),
    }).then((response) => {
      if (response.ok) {
        response.json().then((json) => {
          toast.success("Item updated!");
          setNewTaskTitle("");
          setNewTaskDesc("");
          setTaskUpdate();
          genericUpdateList();
        });
      } else {
        response.json().then((json) => {
          toast.error(json.error);
        });
      }
    });
  };

  const deleteItem = (taskDelete) => {
    fetch(`${baseUrl}/todo/delete/${taskDelete.id}`, {
      // mode: 'no-cors',
      method: "DELETE",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    }).then((response) => {
      if (response.ok) {
        response.json().then((json) => {
          toast.success("Item deleted!");
          genericUpdateList();
        });
      } else {
        response.json().then((json) => {
          toast.error(json.error);
        });
      }
    });
  };

  const setInProgress = (task) => {
    fetch(`${baseUrl}/todo/update/${task.id}/in_progress`, {
      // mode: 'no-cors',
      method: "POST",
    }).then((response) => {
      if (response.ok) {
        response.json().then((json) => {
          toast.success("Item updated!");
          genericUpdateList();
        });
      } else {
        response.json().then((json) => {
          toast.error(json.error);
        });
      }
    });
  };

  const setCompleted = (task) => {
    fetch(`${baseUrl}/todo/update/${task.id}/completed`, {
      // mode: 'no-cors',
      method: "POST",
    }).then((response) => {
      if (response.ok) {
        response.json().then((json) => {
          toast.success("Item updated!");
          genericUpdateList();
        });
      } else {
        response.json().then((json) => {
          toast.error(json.error);
        });
      }
    });
  };

  return (
    <>
      <main className={styles.main}>
        <div className={styles.sides}>
          <h2>Tasks:</h2>
          <input
            type="text"
            placeholder="Filter"
            onChange={handleChangeFilter}
          ></input>
          <ul className={styles.items}>
            {tasks?.map((t, index) => {
              return (
                <li key={index} className={styles.item}>
                  <div className={styles.itemBox}>
                    <div className={styles.title}>
                      <span>
                        {index + 1}. {t.title}{" "}
                      </span>
                      <span className={styles.badge}>{t.status}</span>
                    </div>
                    <span className={styles.small_font}>{t.description}</span>
                    <div className={styles.options}>
                      <input
                        type="button"
                        value={"Update"}
                        onClick={() => setToUpdate(tasks[index])}
                      ></input>
                      <input
                        type="button"
                        value={"Delete"}
                        onClick={() => deleteItem(tasks[index])}
                      ></input>
                      {t.status == "PENDING" ? (
                        <input
                          type="button"
                          value={"Change to In Progress"}
                          onClick={() => setInProgress(tasks[index])}
                        ></input>
                      ) : (
                        <></>
                      )}
                      {t.status == "IN_PROGRESS" ? (
                        <input
                          type="button"
                          value={"Change to Completed"}
                          onClick={() => setCompleted(tasks[index])}
                        ></input>
                      ) : (
                        <></>
                      )}
                    </div>
                  </div>
                </li>
              );
            })}
            <div className={styles.pagination}>
              <span
                onClick={() => {
                  if (currentPage > 1)
                    setCurrentPage((currentPage) => currentPage - 1);
                }}
              >
                &lt;
              </span>
              <span>
                Page {currentPage} of {totalPages}
              </span>
              <span
                onClick={() => {
                  if (currentPage < totalPages)
                    setCurrentPage((currentPage) => currentPage + 1);
                }}
              >
                &gt;
              </span>
            </div>
          </ul>
        </div>
        <div className={styles.container}>
          <h2>Add New:</h2>
          <input
            type="text"
            placeholder="Title"
            value={newTaskTitle}
            onChange={(e) => setNewTaskTitle(e.target.value)}
          ></input>
          <textarea
            placeholder="Description"
            rows={15}
            value={newTaskDesc}
            onChange={(e) => setNewTaskDesc(e.target.value)}
          ></textarea>
          {taskUpdate ? (
            <>
              <input type="button" value={"Update"} onClick={updateItem} />
              <input
                type="button"
                value={"Back to Insert"}
                onClick={() => {
                  setNewTaskTitle("");
                  setNewTaskDesc("");
                  setTaskUpdate();
                }}
              />
            </>
          ) : (
            <input type="button" value={"Insert"} onClick={addNewItem} />
          )}
        </div>
      </main>
      <ToastContainer
        position="top-right"
        autoClose={2000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition={Flip}
      />
    </>
  );
}

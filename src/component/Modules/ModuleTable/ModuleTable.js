import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../../sass/Module/ModuleTable/ModuleTable.scss'
import AddModule from '../AddModule';
import { faCube, faMagnifyingGlass, faPlusCircle } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare } from '@fortawesome/free-regular-svg-icons';
import { TableRow, Table, TableHead, TableCell, TableBody, TablePagination } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPenToSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons'
import {
} from '@fortawesome/free-solid-svg-icons'
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { IP } from '../../Layout/constaints';
import Confirm from '../../action/Confirm';

const ModuleTable = () => {
    const [modules, setModules] = useState([])
    const [keyword, setKeyWord] = useState("")
    const [isOpen, openCloseWindow] = useState(false);
    const [isEditModule, setEditModule] = useState(
        {
            "id": null,
            "name": "",
            "path": "",
            "caption": "",
            "argDefalt": "",
            "note": ""
        }
    )
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowPerPage] = useState(10)
    const [deletingModule, setDeletingModule] = useState({
        "id": null,
        "name": "",
        "message": "Are you sure to remove",
        "note": "This module will be removed permanently",
    })
    const [isOpenDeleteScreen, setOpenDeleteScreen] = useState(false)
    useEffect(() => {
        getModules()
    }, [keyword])
    // Hàm đóng mở cửa sổ thêm mới module hoặc edit module
    const handleOpenWindow = (id) => {
        openCloseWindow(true)
        setEditModule({
            ...isEditModule,
            "id": id
        })
    }
    const handleCloseWindow = () => {
        openCloseWindow(false)
        getModules()
    }
    const getModules = () => {
        fetch("/api/v1/modules")
            .then(respone => respone.json())
            .then(data => {
                let renderData = data.filter(module => module.name.toLowerCase().includes(keyword.toLowerCase()));
                setModules(renderData)
            })
            .catch(err => console.log(err))
    }
    /*Phân trang*/
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    }
    const handleChangeRowsPerPage = (event) => {
        setRowPerPage(parseInt(event.target.value), 10)
        setPage(0)
    }

    /**Xoa module */
    const removeModule = (id, name) => {
        setOpenDeleteScreen(true)
        setDeletingModule({
            ...deletingModule,
            "id": id,
            "name": name
        })
    }
    const deleteModule = (id, userChoice) => {
        if (userChoice) {
            const options = {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                }
            };
            fetch("/api/v1/module?id=" + id, options)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    notify(data.message, data.code)
                    getModules()
                }
                )
                .catch(err => console.log(err));
        }
    }
    const notify = (message, status) => {
        if (status == 1) {
            toast.success(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }
        else if (status == 0) {
            toast.error(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }
        else {
            toast.warn(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    return (
        <div className='Module_Table'>
            <div className='searchBar d-flex justify-content-between align-items-center'>
                <div className='searchName'>
                    <input type='text' id="module_name" placeholder='Search by name...'
                        onChange={(e) => {
                            setKeyWord(e.target.value)
                        }}
                    ></input>
                    <FontAwesomeIcon icon={faMagnifyingGlass} flip="horizontal" ></FontAwesomeIcon>
                </div>
                <div className='addModule'>
                    <button className='addModuleButton d-flex' onClick={() => handleOpenWindow(null)}>
                        <div className='addModuleButton-icon'>
                            <FontAwesomeIcon icon={faPlusSquare}></FontAwesomeIcon>
                        </div>
                        <div className='addModuleButton-text'>NEW MODULE</div>
                    </button>
                </div>
            </div>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell className="id">Id</TableCell>
                        <TableCell className="module_name">Module name</TableCell>
                        <TableCell className="caption">Caption</TableCell>
                        <TableCell className='arg'>Default argument</TableCell>
                        <TableCell className='note'>Note</TableCell>
                        <TableCell className='actions'>Action</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        modules.map(module => {
                            return (
                                <TableRow>
                                    <TableCell className="id">{module.id}</TableCell>
                                    <TableCell className="module_name">{module.name}</TableCell>
                                    <TableCell className="caption">{module.caption}</TableCell>
                                    <TableCell className='arg'>{module.argDefalt}</TableCell>
                                    <TableCell className='note'>{module.note}</TableCell>
                                    <TableCell className='actions'>
                                        <div className='actions-container d-flex justify-content-around'>
                                            <div className='action'>
                                                <button
                                                    onClick={() => {
                                                        handleOpenWindow(module.id)
                                                    }}
                                                >
                                                    <FontAwesomeIcon icon={faPenToSquare} style={{ color: "#699BF7" }} />
                                                </button>
                                            </div>
                                            <div className='action'>
                                                <button onClick={() => {
                                                    removeModule(module.id, module.name)
                                                }}>
                                                    <FontAwesomeIcon icon={faTrashCan} style={{ color: "#FFD233" }} />
                                                </button>
                                            </div>
                                        </div>
                                    </TableCell>
                                </TableRow>
                            )
                        })
                    }
                </TableBody>
            </Table>
            {
                modules.length == 0 ? false : true && <TablePagination
                    rowsPerPageOptions={[10, 15, 20]}
                    component="div"
                    count={modules.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                ></TablePagination>
            }
            {isOpen && <AddModule handleCloseWindow={handleCloseWindow} id={isEditModule.id}  ></AddModule>}
            {
               isOpenDeleteScreen &&<Confirm confirmContent={deletingModule} listDelete={[]} setOpenDeleteScreen={setOpenDeleteScreen} handleFunction={deleteModule} ></Confirm>
            }
            <ToastContainer></ToastContainer>
        </div>
    )
}
export default ModuleTable;
import { useState, useEffect, useRef } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCube, faMagnifyingGlass, faClockRotateLeft, faArrowRotateBack } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons';
import { Checkbox, Pagination } from "@mui/material";
import DropDownInput from "../action/DropDownInput";
import { IP } from "../Layout/constaints";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../sass/RecycleBin/RecycleBin.scss'
import RecycleBinTable from "./RecycleBinTable/RecycleBinTable";
const RecycleBin = () => {
    const [probes, setProbes] = useState([])
    const [selectedProbe, setSelectedProbe] = useState("")
    const [page, setPage] = useState(1)
    const [totalPage, setTotalPage] = useState(0)
    useEffect(() => {
        fetch("http://" + IP + ":8081/api/v1/probes/recycle")
            .then(response => response.json())
            .then(data => {
                setProbes(data)
            })
    }, [])
    const handleChangePage = (event, newPage) => {
        setPage(newPage)
    }
    return (
        <div className="RecycleBin">
            <div className='searchBar d-flex justify-content-between align-items-end'>
                <div className='searchTitle'>
                    <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90} style={{color: "#ffffff",}} />
                    <input 
                        type='text' 
                        placeholder='Search by probe name...' 
                        id="content"
                    ></input>
                </div>
            </div>
            <RecycleBinTable probes={probes} setProbes={setProbes}  ></RecycleBinTable>
            <div className='pagination d-flex justify-content-center'>
                <Pagination count={totalPage}
                    siblingCount={1}
                    color="secondary"
                    onChange={handleChangePage}
                    page={page}
                    showFirstButton showLastButton
                    boundaryCount={2}
                ></Pagination>
            </div>
            <ToastContainer></ToastContainer>
        </div>
    )
}
export default RecycleBin;
import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { Pagination } from "@mui/material";
import { IP } from "../Layout/constaints";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../sass/RecycleBin/RecycleBin.scss'
import RecycleBinTable from "./RecycleBinTable/RecycleBinTable";
const RecycleBin = () => {
    const [probes, setProbes] = useState([])
    const [selectedProbe, setSelectedProbe] = useState("")
    const [page, setPage] = useState(1)
    const [totalPage, setTotalPage] = useState(1)
    const [nameCondition,setNameCondition] = useState("")
    useEffect(() => {
        getProbesInBin(nameCondition)
    }, [page,nameCondition])
    const getProbesInBin = (nameCondition) => {
        const fetchData = () => {
            let api = "http://" + IP + "/api/v1/probes/recycle?page=" + (page - 1);
            if (nameCondition != "") {
                api += "&name=" + nameCondition;
            }
            fetch(api)
                .then(response => response.json())
                .then(data => {
                    if (data.length === 0) {
                        setTotalPage(0)
                    }
                    else setTotalPage(data[0].totalPage)
                    setProbes(data)
                })
                .catch(err => console.log(err))
        }
        fetchData()
    }
    const handleChangePage = (event, newPage) => {
        console.log(newPage)
        setPage(newPage)
    }

    return (
        <div className="RecycleBin">
            <div className='searchBar d-flex justify-content-between align-items-end'>
                <div className='searchTitle'>
                    <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90} style={{ color: "#ffffff", }} />
                    <input
                        type='text'
                        placeholder='Search by probe name...'
                        id="content"
                        onChange={(e)=>{
                            setNameCondition(e.target.value)
                        }}
                    ></input>
                </div>
            </div>
            <RecycleBinTable probes={probes} getProbesInBin={getProbesInBin} nameCondition={nameCondition} ></RecycleBinTable>
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
import '../../sass/Probes/DuplicateProbe.scss'
import { useState, useEffect, useContext } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faPaste } from '@fortawesome/free-regular-svg-icons'
import {
faFloppyDisk, faCopy, faClone
} from '@fortawesome/free-solid-svg-icons'
import DropDownInput from '../action/DropDownInput';
import {  toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ProbesContext } from './ProbesTable/ProbesContext';
import { IP } from '../Layout/constaints';
import NotifyContainer from '../RecycleBin/RecycleBinTable/NotifyContainer';
const DuplicateProbe = (props) => {
    const { handleCloseWindow } = props
    const [probes, setProbes] = useState([]);
    const [selectedProbe,setSelectedProbe] = useState();
    const [duplicateNumber, setDuplicateNumber] = useState(1)
    const [notifyList, setNotifyList] = useState([])
    const [notifyScreen, setOpenNotifyScreen] = useState(false)
    const probeContext = useContext(ProbesContext)
    useEffect(() => {
        fetch( IP + "/api/v1/probes?name=&location=&area=&vlan=")
            .then(response => response.json())
            .then(data => {
                let arr = []
                data.map(ele => {
                    arr.push({
                        label: ele.name,
                        value: ele.id
                    })
                })
                setProbes(arr)
            })
    }, [])
    const getData = () =>{
        /** get data of origin probe */
        let originProbe = selectedProbe;
        let info = document.getElementById("note").value
        let strArray = info.split('\n')
        let listProbe = []
        let errorLine = []
        strArray.map((line,i) =>{
            try {
                let part = line.split(":")
                console.log(part)
                if(part.length===2){
                    listProbe.push({
                        name: part[0],
                        ip: part[1]
                    })
                }
                else errorLine.push(i+1)
            } catch (error) {
                errorLine.push(i+1)
            }
        })
        return {
            data:{
                listProbe:listProbe,
                probeOrigin: originProbe
            },
            errorLine: errorLine
        }
    }
    const handleChangeRow = (event) => {
        const numberOfLines = event.target.value.split('\n').length
        // console.log(numberOfLines)
        let lineNumber = document.querySelector(".line-number")
        lineNumber.innerHTML = Array(numberOfLines).fill('<div></div>').join("")
        setDuplicateNumber(numberOfLines)
    };
    const handleDuplicate = ()=>{
        let checkData = getData()
        if(checkData.data.probeOrigin==null){
            notify(`You have not choosen the origin probe yet.`,2)
        }
        else if(checkData.data.listProbe.length===0 && checkData.errorLine.length===0 ){
            notify(`The information of new probes are missing`,2)
        }
        else if(checkData.errorLine.length!==0){
            let message= "Input format in line number "
            for(let i = 0;i<checkData.errorLine.length;i++){
                if(i===checkData.errorLine.length-1){
                    message+= checkData.errorLine[i]
                }
                else message+= checkData.errorLine[i]+", "
            }
            message+= " is wrong. Please check carefully"
            notify(message,2)
        }
        else{
            console.log(checkData.data)
            let options = {
                method: "POST",
                headers:{
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(checkData.data) 
            }
            fetch(" http://"+IP+"/api/v1/duplicate",options)
                .then(response => response.json())
                .then(data => {
                    probeContext.getProbes()
                    setOpenNotifyScreen(true)
                    setNotifyList(data)
                })
                .then(err => console.log(err))
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
                autoClose: false,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    return (
        <div>
            <div className='duplicateProbeScreen'>
                <div className="duplicateProbe">
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    </div>
                    {/* IP Address & Password */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faCopy} />
                                <div className='input_container-icon-text'>DUPLICATED PROBE</div>
                            </div>
                            <div className='input_container-input'>
                                <DropDownInput defaultContent="Choose a probe to duplicate" inputOptions={probes} handleSelect={setSelectedProbe} needAll={true} ></DropDownInput>
                            </div>
                        </div>
                        <div className='input_container' style={{ width: "20%" }}>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faPaste} />
                                <div className='input_container-icon-text'>DUPLICATE AMOUNT</div>
                            </div>
                            <div className='input_container-input '>
                                <input type='number' value={duplicateNumber} disabled id='dup_amount'
                                ></input>
                            </div>
                        </div>
                    </div>
                    <div className="field d-flex justify-content-between">
                        <div className='input_container exception'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faClone} />
                                <div className='input_container-icon-text'>NEW PROBE INFORMATION</div>
                            </div>
                            <div className='input_container-input'>
                                <div className='editor d-flex'>
                                    <div className='line-number'>
                                    </div>
                                    <textarea placeholder='Please type this format "probe_name:ip_address" on each row' id='note'
                                        rows={duplicateNumber}
                                        onChange={handleChangeRow}
                                    >
                                    </textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/* Button */}
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='btn btn-success d-flex align-items-center'
                            onClick={handleDuplicate}
                        >
                            <div className='btn-icon d-flex align-items-center' >
                                <FontAwesomeIcon icon={faFloppyDisk} />
                            </div>
                            <div className='btn-text' >Save</div>
                        </button>
                    </div>
                </div>
            </div>
            {notifyScreen&& <NotifyContainer notifyList={notifyList} setOpenNotifyScreen={setOpenNotifyScreen} handleCloseWindow={handleCloseWindow}></NotifyContainer>}
        </div>
    )
}
export default DuplicateProbe;
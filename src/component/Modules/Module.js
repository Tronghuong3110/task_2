import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../../sass/Module/Module.scss'
import { faCube, faMagnifyingGlass, faPlusCircle } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare } from '@fortawesome/free-regular-svg-icons';
import ModuleTable from './ModuleTable/ModuleTable';
import AddModule from './AddModule';
const Module = () =>{
    useEffect(()=>{
        document.querySelector(".header-name").textContent = "MODULES"
    },[])
    return (
        <div className="module">
            <ModuleTable></ModuleTable>
        </div>
    )
}
export default Module;
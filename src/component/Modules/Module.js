import {  useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Module/Module.scss'
import ModuleTable from './ModuleTable/ModuleTable';
const Module = () =>{
    return (
        <div className="module">
            <ModuleTable></ModuleTable>
        </div>
    )
}
export default Module;
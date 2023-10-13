import { createContext,useEffect,useState } from "react";

const ProbeDetailsContext = createContext();
const ProbeDetailsProvider = ({children}) =>{
    
    return (
        <ProbeDetailsContext.Provider>
            {children}
        </ProbeDetailsContext.Provider>
    )
}
export {ProbeDetailsContext,ProbeDetailsProvider}
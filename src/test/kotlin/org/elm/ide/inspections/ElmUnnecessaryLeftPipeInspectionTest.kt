package org.elm.ide.inspections


class ElmUnnecessaryLeftPipeInspectionTest : ElmInspectionsTestBase(ElmUnnecessaryLeftPipeInspection()) {

    fun `test is not redundant`() = checkByText("""
f2 x = h <| \y -> y        
""")

    fun `test is redundant - list literal`() = checkByText("""
f1 x h = h <warning descr="'<|' is redundant"><|</warning> []
""")

    fun `test is redundant - record literal`() = checkByText("""
f1 x h = h <warning descr="'<|' is redundant"><|</warning> {}
""")

    fun `test is redundant - paren expr`() = checkByText("""
f1 x h = h <warning descr="'<|' is redundant"><|</warning> (x)
""")

    fun `test is redundant - unit expr`() = checkByText("""
f1 x h = h <warning descr="'<|' is redundant"><|</warning> ()
""")

    fun `test is redundant - string expr`() = checkByText("""
f1 x h = h <warning descr="'<|' is redundant"><|</warning> ""
""")


    fun `test safe delete fix available when left pipe is redundant`() = checkFixByText("Safely Delete <|",
            """
f1 x h = h <warning descr="'<|' is redundant"><|{-caret-}</warning> []
""", """
f1 x h = h []
""")

}
